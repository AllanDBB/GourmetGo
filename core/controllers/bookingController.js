const Booking = require('../models/bookingSchema');
const Experience = require('../models/experienceSchema');
const mailer = require('../utils/mailer');
const { generateBookingCode, generateQRCodes } = require('../utils/bookingUtils');
const { createBookingPDF } = require('../utils/pdfkit');
const path = require('path');
const fs = require('fs');

exports.createBooking = async (req, res) => {
  try {
    const userId = req.user.userId;
    const { experienceId, people, name, email, phone, termsAccepted, paymentMethod } = req.body;

    // Validaciones básicas
    if (!experienceId || !people || !name || !email || !phone || !termsAccepted || !paymentMethod) {
      return res.status(400).json({ message: 'Todos los campos son obligatorios.' });
    }
    if (!['Pago en el lugar', 'Transferencia'].includes(paymentMethod)) {
      return res.status(400).json({ message: 'Método de pago inválido.' });
    }
    if (!termsAccepted) {
      return res.status(400).json({ message: 'Debe aceptar los términos y condiciones.' });
    }

    // Buscar experiencia
    const experience = await Experience.findById(experienceId);
    if (!experience) return res.status(404).json({ message: 'Experiencia no encontrada.' });

    // Verificar si la experiencia está activa
    if (experience.status !== 'Activa') {
      return res.status(400).json({ message: 'La experiencia no está disponible para reservas.' });
    }

    // Validar capacidad
    if (people > experience.remainingCapacity) {
      return res.status(400).json({ message: 'No hay suficiente capacidad disponible.' });
    }

    const existingBooking = await Booking.findOne({
      user: userId,
      experience: experienceId,
      name: name.trim()
    });
    if (existingBooking) {
      return res.status(400).json({
        message: 'Ya tienes una reservación para este evento con ese nombre. Usa un nombre diferente para otra reservación.'
      });
    }

    // Generar código único y QRs
    const bookingCode = generateBookingCode();
    const qrCodes = await generateQRCodes(bookingCode, people);

    // Crear reserva
    const booking = new Booking({
      user: userId, // <- asocia la reserva al usuario autenticado
      experience: experience._id,
      people,
      name,
      email,
      phone,
      termsAccepted,
      paymentMethod,
      status: 'pending',
      bookingCode,
      qrCodes
    });
    await booking.save();

    // Actualizar capacidad restante de la experiencia
    experience.remainingCapacity -= people;
    await experience.save();


    // Generar PDF en memoria (Buffer)
    const pdfBuffer = await createBookingPDF({
      name,
      experienceTitle: experience.title,
      date: experience.date.toLocaleString(),
      people,
      qrCodes
    });

    // Enviar correo con PDF adjunto (Buffer)
    await mailer.sendMailTemplate(
      email,
      'Confirmación de reservación',
      'booking-confirmation.html',
      {
        name,
        experienceTitle: experience.title,
        date: experience.date.toLocaleString(),
        people,
        paymentMethod,
        bookingCode,
        year: new Date().getFullYear()
      },
      [
        {
          filename: 'entradas.pdf',
          content: pdfBuffer
        }
      ]
    );

    res.status(201).json({ message: 'Reservación realizada exitosamente.', booking });
  } catch (err) {
    res.status(500).json({ message: 'Error al crear la reservación.', error: err.message });
  }
};

exports.getMyBookings = async (req, res) => {
  try {
    const userId = req.user.userId;
    const bookings = await Booking.find({ user: userId }).populate('experience');
    res.json(bookings);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener las reservaciones.', error: err.message });
  }
};

exports.getBookingDetail = async (req, res) => {
  try {
    const booking = await Booking.findById(req.params.id)
      .populate('user', 'name email avatar')
      .populate('experience');
    if (!booking) return res.status(404).json({ message: 'Reservación no encontrada.' });
    
    if (booking.user._id.toString() !== req.user.userId && req.user.role !== 'admin') {
      return res.status(403).json({ message: 'No autorizado.' });
    }

    res.json(booking);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener el detalle de la reservación.', error: err.message });
  }
};

exports.cancelBooking = async (req, res) => {
  try {
    const userId = req.user.userId;
    const booking = await Booking.findOne({ _id: req.params.id, user: userId });

    if (!booking) {
      return res.status(404).json({ message: 'Reservación no encontrada.' });
    }
    if (booking.status !== 'pending') {
      return res.status(400).json({ message: 'Solo puedes cancelar reservas pendientes.' });
    }

    booking.status = 'cancelled';
    await booking.save();

    const experience = await Experience.findById(booking.experience);
    if (experience) {
      experience.remainingCapacity += booking.people;
      await experience.save();
    }

    // Enviar correo de cancelación
    await mailer.sendMailTemplate(
      booking.email,
      'Reservación cancelada',
      'booking-cancelled.html',
      {
        name: booking.name,
        experienceTitle: experience ? experience.title : '',
        date: experience ? experience.date.toLocaleString() : '',
        people: booking.people,
        bookingCode: booking.bookingCode,
        year: new Date().getFullYear()
      }
    );

    res.json({ message: 'Reservación cancelada exitosamente.' });
  } catch (err) {
    res.status(500).json({ message: 'Error al cancelar la reservación.', error: err.message });
  }
};

exports.getChefBookings = async (req, res) => {
  try {
    const chefId = req.params.id;

    if (req.user.userId !== chefId && req.user.role !== 'chef') {
      return res.status(403).json({ message: 'No autorizado.' });
    } 

    const experiences = await Experience.find({ chef: chefId });
    if (!experiences || experiences.length === 0) {
      return res.status(404).json({ message: 'No se encontraron experiencias para este chef.' });
    }

    const experienceIds = experiences.map(exp => exp._id);
    const bookings = await Booking.find({ experience: { $in: experienceIds } })
      .populate('user', 'name email avatar')
      .populate('experience', 'title date remainingCapacity');
      
    res.json(bookings);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener las reservaciones del chef.', error: err.message });
  }
};


exports.getExperienceBookings = async (req, res) => {
  try {
    const experienceId = req.params.id;
    const bookings = await Booking.find({ experience: experienceId })
      .populate('user', 'name email avatar')
      .populate('experience', 'title date remainingCapacity');
      
    if (!bookings || bookings.length === 0) {
      return res.status(404).json({ message: 'No se encontraron reservaciones para esta experiencia.' });
    }

    res.json(bookings);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener las reservaciones de la experiencia.', error: err.message });
  }
};
