const User = require('../models/userSchema');
const ChefProfile = require('../models/chefProfileSchema');
const { validateUserUpdate } = require('../utils/validators');

exports.updateMe = async (req, res) => {
  try {
    const userId = req.user.userId; 
    const { name, password, ...fieldsToUpdate } = req.body;

    if (name !== undefined || password !== undefined) {
      return res.status(400).json({ message: 'No se puede modificar el nombre ni la contraseña.' });
    }

    const error = validateUserUpdate(fieldsToUpdate);
    if (error) return res.status(400).json({ message: error });

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { $set: fieldsToUpdate },
      { new: true }
    ).select('-password');

    res.json({ message: 'Perfil actualizado correctamente.', user: updatedUser });
  } catch (err) {
    res.status(500).json({ message: 'Error al actualizar el perfil.', error: err.message });
  }
};

exports.getMyProfile = async (req, res) => {
  try {
    const user = await User.findById(req.user.userId).select('-password');
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado.' });

    if (user.role === 'chef') {
      const chefProfile = await ChefProfile.findOne({ user: user._id });
      if (chefProfile) {
        const chefData = {
          ...user.toObject(),
          contactPerson: chefProfile.contactPerson,
          bio: chefProfile.bio,
          experience: chefProfile.experience,
          socialLinks: chefProfile.socialLinks
        };
        return res.json(chefData);
      }
    }

    res.json(user);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener el perfil.', error: err.message });
  }
};

exports.getPublicProfile = async (req, res) => {
  try {
    const user = await User.findById(req.params.id).select('name avatar preferences');
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado.' });
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener el perfil público.', error: err.message });
  }
};


exports.changePassword = async (req, res) => {
  try {
    const userId = req.user.userId;
    const { currentPassword, newPassword } = req.body;

    if (!currentPassword || !newPassword) {
      return res.status(400).json({ message: 'Por favor, proporciona la contraseña actual y la nueva.' });
    }

    const user = await User.findById(userId);
    if (!user) return res.status(404).json({ message: 'Usuario no encontrado.' });

    const isMatch = await user.comparePassword(currentPassword);
    if (!isMatch) return res.status(400).json({ message: 'Contraseña actual incorrecta.' });

    user.password = newPassword;
    await user.save();

    res.json({ message: 'Contraseña cambiada correctamente.' });
  } catch (err) {
    res.status(500).json({ message: 'Error al cambiar la contraseña.', error: err.message });
  }
};