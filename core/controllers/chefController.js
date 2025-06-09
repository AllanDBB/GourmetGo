const ChefProfile = require('../models/chefProfileSchema');
const { validateChefUpdate } = require('../utils/validators');
const Experience = require('../models/experienceSchema');

exports.createChefProfile = async ({ userId, contactPerson, location, cuisineType, bio, experience, socialLinks }) => {
  const chefProfile = new ChefProfile({
    user: userId,
    contactPerson,
    location,
    cuisineType,
    bio,
    experience,
    socialLinks
  });
  await chefProfile.save();
  return chefProfile;
};

exports.updateMe = async (req, res) => {
  try {
    const userId = req.user.userId; // Asume autenticación JWT
    const { contactPerson, phone, location, cuisineType, photoUrl, bio, experience, socialLinks, name, password } = req.body;

    // No permitir modificar nombre ni contraseña
    if (name !== undefined || password !== undefined) {
      return res.status(400).json({ message: 'No se puede modificar el nombre ni la contraseña.' });
    }

    // Validar los campos a modificar
    const error = validateChefUpdate(req.body);
    if (error) return res.status(400).json({ message: error });

    // Buscar el perfil de chef por el userId
    const chefProfile = await ChefProfile.findOneAndUpdate(
      { user: userId },
      { $set: { contactPerson, phone, location, cuisineType, bio, experience, socialLinks } },
      { new: true }
    );

    // Update profile picture:
    if (photoUrl) {
      chefProfile.avatar = photoUrl;
      await chefProfile.save();
    }



    if (!chefProfile) {
      return res.status(404).json({ message: 'Perfil de chef no encontrado.' });
    }

    res.json({ message: 'Perfil de chef actualizado correctamente.', chef: chefProfile });
  } catch (err) {
    res.status(500).json({ message: 'Error al actualizar el perfil de chef.', error: err.message });
  }
};

exports.getChefExperiences = async (req, res) => {
  try {
    const chefId = req.params.id;
    const experiences = await Experience.find({ chef: chefId });
    res.json(experiences);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener las experiencias del chef.', error: err.message });
  }
};

