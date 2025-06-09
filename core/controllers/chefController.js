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
    const { contactPerson, phone, location, cuisineType, photoUrl, bio, experience, socialLinks } = req.body;


    // Validar los campos a modificar
    const error = validateChefUpdate(req.body);
    if (error) return res.status(400).json({ message: error });

    // Buscar usuario:
    const userProfile = await User.findById(userId);

    if (!userProfile) {
      return res.status(404).json({ message: 'Usuario no encontrado.' });
    }

    // Actualizar el perfil del usuario (phone, location, photoUrl, name, password)
    userProfile.phone = phone || userProfile.phone;
    userProfile.location = location || userProfile.location;
    userProfile.avatar = photoUrl || userProfile.avatar;
    userProfile.preferences = cuisineType ? [cuisineType] : userProfile.preferences;

    await userProfile.save();

    // Buscar o crear el perfil de chef
    let chefProfile = await ChefProfile.findOne({ user: userId });
    
    chefProfile.contactPerson = contactPerson || chefProfile.contactPerson;
    chefProfile.bio = bio || chefProfile.bio;
    chefProfile.experience = experience || chefProfile.experience;
    chefProfile.socialLinks = socialLinks || chefProfile.socialLinks;

    await chefProfile.save();

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

