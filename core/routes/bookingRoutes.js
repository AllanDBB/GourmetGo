const express = require('express');
const router = express.Router();
const bookingController = require('../controllers/bookingController');
const auth = require('../middlewares/auth');

// Crear reservación (requiere login)
router.post('/', auth, bookingController.createBooking);

// Listar reservaciones del usuario autenticado
router.get('/my', auth, bookingController.getMyBookings);

// Detalle de reserva (requiere login)
router.get('/:id', auth, bookingController.getBookingDetail);

// Cancelar reserva (requiere login)
router.put('/:id/cancel', auth, bookingController.cancelBooking);

// Reservas de mis experiencias (chef)
router.get('/chefs/:id/bookings', auth, bookingController.getChefBookings);

// Reservas de una experiencia específica
router.get('/experiences/:id/bookings', auth, bookingController.getExperienceBookings);

module.exports = router;