const User = require('../models/userSchema');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const chefController = require('./chefController');
const mailer = require('../utils/mailer');
const { validateUserRegistration, validateChefRegistration } = require('../utils/validators');

// Normal user registration
exports.registerUser = async (req, res) => {
  try {
    const { name, email, phone, identification, password, photoUrl, preferences = [] } = req.body;

    const existingUser = await User.findOne({ email });
    if (existingUser) return res.status(400).json({ message: 'El correo electrónico ya está en uso.' });

    const hashedPassword = await bcrypt.hash(password, 10);
    const user = new User({
      name,
      email,
      phone,
      identification,
      password: hashedPassword,
      avatar: photoUrl,
      preferences,
      role: 'user'
    });
    await user.save();

    // Enviar correo de bienvenida
    await mailer.sendMailTemplate(
      user.email,
      '¡Bienvenido a GourmetGo!',
      'welcome-user.html',
      {
        name: user.name,
        year: new Date().getFullYear()
      }
    );

    res.status(201).json({ message: 'Usuario registrado exitosamente.' });
  } catch (err) {
    res.status(500).json({ message: 'Error en el registro.', error: err.message });
  }
};

// Chef registration
exports.registerChef = async (req, res) => {
  try {
    const {
      name, 
      contactPerson,
      email,
      phone,
      location,
      cuisineType,
      password,
      photoUrl,
      bio = '',
      experience = '',
      socialLinks = []
    } = req.body;

    const existingUser = await User.findOne({ email });
    if (existingUser) return res.status(400).json({ message: 'El correo electrónico ya está en uso.' });
  
    const hashedPassword = await bcrypt.hash(password, 10);

    const user = new User({
      name,
      email,
      phone,
      password: hashedPassword,
      avatar: photoUrl,
      role: 'chef',
      location,
      preferences: [cuisineType]
    });
    await user.save();

    await chefController.createChefProfile({
      userId: user._id,
      contactPerson,
      location,
      cuisineType,
      bio,
      experience,
      socialLinks
    });

    await mailer.sendMailTemplate(
      user.email,
      '¡Bienvenido a GourmetGo!',
      'welcome-chef.html',
      {
        name: user.name,
        year: new Date().getFullYear()
      }
    );

    res.status(201).json({ message: 'Chef o restaurante registrado exitosamente.' });
  } catch (err) {
    res.status(500).json({ message: 'Error en el registro de chef.', error: err.message });
  }
};

exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;
    
    // convert mail to lowercase:
    if (!email || !password) {
      return res.status(400).json({ message: 'Por favor, proporciona un correo electrónico y una contraseña.' });
    }

    const normalizedEmail = email.toLowerCase();
    const user = await User.findOne({ email: normalizedEmail });
    if (!user) return res.status(400).json({ message: 'Credenciales inválidas.' });

    const valid = await bcrypt.compare(password, user.password);
    if (!valid) return res.status(400).json({ message: 'Credenciales inválidas.' });

    const token = jwt.sign(
      { userId: user._id, role: user.role },
      process.env.JWT_SECRET || 'secret',
      { expiresIn: '1d' }
    );

    res.json({ token, user: { id: user._id, name: user.name, email: user.email, role: user.role } });
  } catch (err) {
    res.status(500).json({ message: 'Error en el login.', error: err.message });
  }
};

exports.logout = (req, res) => {
  res.json({ message: 'Logout successful (handled on frontend)' });
};

exports.refresh = (req, res) => {
  res.json({ message: 'Refresh endpoint (implement if using refresh tokens)' });
};