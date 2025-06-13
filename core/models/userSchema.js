const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, unique: true, required: true },
  password: { type: String, required: true },
  phone: { type: String, unique: true, required: true },
  identification: { type: String, unique: true, required: false },
  role: { type: String, enum: ['user', 'chef'], default: 'user' },
  avatar: String,
  location: { type: String, default: '' },
  preferences: [String],
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', UserSchema);