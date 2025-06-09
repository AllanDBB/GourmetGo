const mongoose = require('mongoose');

const ExperienceSchema = new mongoose.Schema({
  title: { type: String, required: true },
  description: { type: String, required: true },
  date: { type: Date, required: true },
  location: { type: String, required: true }, // URL
  capacity: { type: Number, required: true, min: 1 },
  remainingCapacity: { type: Number, required: true, min: 0 },
  price: { type: Number, required: true, min: 0.01 },
  duration: { type: Number, required: true, min: 0.1 }, // horas, puede ser decimal
  category: { type: String, required: true },
  images: [{ type: String, required: true }], // URLs de Cloudinary
  requirements: { type: String }, // Opcional
  status: { type: String, enum: ['Activa', 'Agotada', 'Próximamente'], required: true },
  menu: {
    image: { type: String }, // URL de Cloudinary (opcional)
    text: { type: String }   // Opcional
  },
  chef: { type: mongoose.Schema.Types.ObjectId, required: true },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Experience', ExperienceSchema);