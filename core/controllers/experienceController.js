const Experience = require('../models/experienceSchema'); 
const { generateDeleteCode } = require('../utils/deleteCode');
const ChefProfile = require('../models/chefProfileSchema');
const mailer = require('../utils/mailer');
const deleteCodes = new Map();
const User = require('../models/userSchema');

exports.createExperience = async (req, res) => {
  try {
    const userId = req.user.userId;

    const chefProfile = await ChefProfile.findOne({ user: userId }).populate('user');
    if (!chefProfile) {
      return res.status(404).json({ message: 'Perfil de chef no encontrado.' });
    }

    const experienceData = { ...req.body, chef: userId };
    const experience = new Experience(experienceData);
    experience.remainingCapacity = experience.capacity; 
    await experience.save();

    // Enviar correo al chef
    await mailer.sendMailTemplate(
      chefProfile.user.email,
      '¡Tu experiencia ha sido publicada!',
      'experience-created.html',
      {
        name: chefProfile.contactPerson || chefProfile.user.name,
        experienceTitle: experience.title,
        date: experience.date.toLocaleString(),
        location: experience.location,
        capacity: experience.capacity,
        duration: experience.duration,
        description: experience.description,
        price: experience.price,
        year: new Date().getFullYear()
      }
    );

    res.status(201).json({ message: 'Experiencia creada exitosamente.', experience });
  } catch (err) {
    res.status(500).json({ message: 'Error al crear la experiencia.', error: err.message });
  }
};

exports.updateExperience = async (req, res) => {
  try {
    const experienceId = req.params.id;
    const { location, date, status, capacity, price } = req.body;

    const experience = await Experience.findById(experienceId).populate({
      path: 'chef',
      populate: { path: 'user' }
    });
    if (!experience) return res.status(404).json({ message: 'Experiencia no encontrada.' });

    if (status && experience.status === 'Próximamente' && status === 'Activa') {
      experience.status = status;
    } else if (status && status !== experience.status) {
      return res.status(400).json({ message: 'Solo se permite cambiar el estado de Próximamente a Activa.' });
    }

    if (location !== undefined) experience.location = location;
    if (date !== undefined) experience.date = date;
    if (capacity !== undefined) experience.capacity = capacity;
    if (price !== undefined) experience.price = price;

    await experience.save();

    // Enviar correo al chef notificando la actualización
    const chefUser = experience.chef.user;
    await mailer.sendMailTemplate(
      chefUser.email,
      '¡Tu experiencia ha sido actualizada!',
      'experience-updated.html',
      {
        name: experience.chef.contactPerson || chefUser.name,
        experienceTitle: experience.title,
        date: experience.date.toLocaleString(),
        location: experience.location,
        capacity: experience.capacity,
        price: experience.price,
        year: new Date().getFullYear()
      }
    );

    res.json({ message: 'Experiencia actualizada correctamente.', experience });
  } catch (err) {
    res.status(500).json({ message: 'Error al actualizar la experiencia.', error: err.message });
  }
};


exports.requestDeleteExperience = async (req, res) => {
  try {
    const { id } = req.params;
    const { email } = req.body;

    // Validar que el correo electrónico sea del chef
    const experience = await Experience.findById(id).populate('chef');

    if (!experience) return res.status(404).json({ message: 'Experiencia no encontrada.' });
    if (experience.status === 'Agotada') return res.status(400).json({ message: 'No se puede eliminar una experiencia agotada.' });

    // Buscar el chef:
    const chefProfile = await ChefProfile.findOne({ user: experience.chef.user._id });
    if (!chefProfile) return res.status(404).json({ message: 'Perfil de chef no encontrado.' });

    // Buscar el usuario del chef:
    const chefUser = await User.findById(chefProfile.user);
    if (!chefUser) return res.status(404).json({ message: 'Usuario del chef no encontrado.' });

    const code = generateDeleteCode();
    deleteCodes.set(`${id}:${email}`, code);

    await mailer.sendMailTemplate(
      email,
      'Código de eliminación de experiencia',
      'delete-experience-code.html',
      {
        contactPerson: chefUser.name,
        experienceTitle: experience.title,
        deleteCode: code,
        year: new Date().getFullYear()
      }
    );

    res.json({ message: 'Código de verificación enviado al correo.' });
  } catch (err) {
    res.status(500).json({ message: 'Error al solicitar código de eliminación.', error: err.message });
  }
};


exports.deleteExperience = async (req, res) => {
  try {
    const { id } = req.params;
    const { email, code } = req.body;

    // Buscar la experiencia y el perfil de chef asociado
    const experience = await Experience.findById(id).populate('chef');
    if (!experience) return res.status(404).json({ message: 'Experiencia no encontrada.' });
    if (experience.status === 'Agotada') return res.status(400).json({ message: 'No se puede eliminar una experiencia agotada.' });

    // Buscar el chefProfile y el usuario del chef
    const chefProfile = await ChefProfile.findOne({ user: experience.chef.user });
    if (!chefProfile) return res.status(404).json({ message: 'Perfil de chef no encontrado.' });

    const chefUser = await User.findById(chefProfile.user);
    if (!chefUser) return res.status(404).json({ message: 'Usuario del chef no encontrado.' });

    // Validar correo
    if (chefUser.email !== email) return res.status(403).json({ message: 'Correo no autorizado.' });

    // Validar código
    const storedCode = deleteCodes.get(`${id}:${email}`);
    if (!storedCode || storedCode !== code) {
      return res.status(400).json({ message: 'Código de verificación incorrecto o expirado.' });
    }

    // Notificar a usuarios con reservas activas
    const bookings = await Booking.find({ experience: id, status: { $in: ['pending', 'confirmed'] } }).populate('user');
    for (const booking of bookings) {
      await mailer.sendMailTemplate(
        booking.email,
        'Cancelación de experiencia',
        'cancel-experience.html',
        {
          name: booking.name,
          experienceTitle: experience.title,
          date: experience.date.toLocaleString(),
          year: new Date().getFullYear()
        }
      );
      // Opcional: marcar la reserva como cancelada
      booking.status = 'cancelled';
      await booking.save();
    }

    // Eliminar experiencia
    await Experience.findByIdAndDelete(id);
    deleteCodes.delete(`${id}:${email}`);

    res.json({ message: 'Experiencia eliminada y usuarios notificados.' });
  } catch (err) { 
    res.status(500).json({ message: 'Error al eliminar la experiencia.', error: err.message });
  }
};

exports.listExperiences = async (req, res) => {
  try {
    const { q, chef, date } = req.query;
    const filter = {};
    if (q) filter.title = { $regex: q, $options: 'i' };
    if (chef) filter.chef = chef;
    if (date) filter.date = date;
    const experiences = await Experience.find(filter).populate('chef', 'name');
    res.json(experiences);
  } catch (err) {
    res.status(500).json({ message: 'Error al listar experiencias.', error: err.message });
  }
};

exports.getExperienceDetail = async (req, res) => {
  try {
    const experience = await Experience.findById(req.params.id).populate('chef', 'name');
    if (!experience) return res.status(404).json({ message: 'Experiencia no encontrada.' });
    res.json(experience);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener la experiencia.', error: err.message });
  }
};