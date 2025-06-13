const ChefProfile = require('../models/chefProfileSchema');
const { validateChefUpdate } = require('../utils/validators');
const Experience = require('../models/experienceSchema');
const User = require('../models/userSchema');

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
    const userId = req.user.userId;
    const { contactPerson, phone, location, cuisineType, photoUrl, bio, experience, socialLinks } = req.body;

    // Validar los campos a modificar
    const error = validateChefUpdate(req.body);
    if (error) return res.status(400).json({ message: error });

    // Buscar usuario:
    const userProfile = await User.findById(userId);
    if (!userProfile) {
      return res.status(404).json({ message: 'Usuario no encontrado.' });
    }

    // Actualizar el perfil del usuario
    userProfile.phone = phone || userProfile.phone;
    userProfile.location = location || userProfile.location;
    userProfile.avatar = photoUrl || userProfile.avatar;
    userProfile.preferences = cuisineType ? [cuisineType] : userProfile.preferences;
    await userProfile.save();

    // Buscar el perfil de chef
    let chefProfile = await ChefProfile.findOne({ user: userId });
    if (!chefProfile) {
      return res.status(404).json({ message: 'Perfil de chef no encontrado.' });
    }
    
    // Actualizar el perfil de chef
    chefProfile.contactPerson = contactPerson || chefProfile.contactPerson;
    chefProfile.location = location || chefProfile.location;
    chefProfile.cuisineType = cuisineType || chefProfile.cuisineType;
    chefProfile.bio = bio || chefProfile.bio;
    chefProfile.experience = experience || chefProfile.experience;
    chefProfile.socialLinks = socialLinks || chefProfile.socialLinks;
    await chefProfile.save();

    // Devolver el perfil completo actualizado
    const updatedProfile = {
      id: userProfile._id,
      name: userProfile.name,
      email: userProfile.email,
      phone: userProfile.phone,
      avatar: userProfile.avatar || '',
      photoUrl: userProfile.avatar || '',
      role: userProfile.role,
      location: userProfile.location,
      preferences: userProfile.preferences,
      contactPerson: chefProfile.contactPerson,
      cuisineType: chefProfile.cuisineType,
      bio: chefProfile.bio,
      experience: chefProfile.experience,
      socialLinks: chefProfile.socialLinks
    };

    res.json({ 
      message: 'Perfil de chef actualizado correctamente.', 
      chef: updatedProfile 
    });
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

