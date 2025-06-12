const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const phoneRegex = /^\d{8}$/;
const idRegex = /^\d{9}$/;
const passwordRegex = /^(?=(?:[^a-zA-Z]*[a-zA-Z]){6,})(?=(?:[^0-9]*[0-9]){4,})(?=.*\.)[a-zA-Z0-9.]{11,}$/;

exports.validateUserRegistration = ({ name, email, phone, identification, password, photoUrl }) => {
  if (!name || !email || !phone || !identification || !password || !photoUrl) {
    return 'Todos los campos obligatorios deben ser completados.';
  }
  if (!emailRegex.test(email)) return 'Formato de correo electrónico inválido.';
  if (!phoneRegex.test(phone)) return 'El número telefónico debe tener 8 dígitos.';
  if (!idRegex.test(identification)) return 'La identificación debe tener 9 dígitos.';
  if (!passwordRegex.test(password)) return 'La contraseña debe tener al menos 6 letras, 4 números y un punto.';
  if (!photoUrl.startsWith('https://res.cloudinary.com/')) return 'La fotografía debe ser una URL válida de Cloudinary.';
  return null;
};

exports.validateChefRegistration = ({ name, contactPerson, email, phone, location, cuisineType, password, photoUrl }) => {
  if (!name || !contactPerson || !email || !phone || !location || !cuisineType || !password || !photoUrl) {
    return 'Todos los campos obligatorios deben ser completados.';
  }
  if (!emailRegex.test(email)) return 'Formato de correo electrónico inválido.';
  if (!phoneRegex.test(phone)) return 'El número telefónico debe tener 8 dígitos.';
  if (!passwordRegex.test(password)) return 'La contraseña debe tener al menos 6 letras, 4 números y un punto.';
  if (!photoUrl.startsWith('https://res.cloudinary.com/')) return 'La fotografía debe ser una URL válida de Cloudinary.';
  return null;
};

exports.validateUserUpdate = (fields) => {
  const { email, phone, identification, photoUrl, preferences } = fields;
  if (email && !emailRegex.test(email)) return 'Formato de correo electrónico inválido.';
  if (phone && !phoneRegex.test(phone)) return 'El número telefónico debe tener 8 dígitos.';
  if (identification && !idRegex.test(identification)) return 'La identificación debe tener 9 dígitos.';
  if (photoUrl && !photoUrl.startsWith('https://res.cloudinary.com/')) return 'La fotografía debe ser una URL válida de Cloudinary.';
  if (preferences && !Array.isArray(preferences)) return 'Las preferencias deben ser un arreglo.';
  return null;
};

exports.validateChefUpdate = (fields) => {
  const { contactPerson, phone, location, cuisineType, photoUrl, bio, experience, socialLinks } = fields;
  if (phone && !/^\d{8}$/.test(phone)) return 'El número telefónico debe tener 8 dígitos.';
  if (photoUrl && !photoUrl.startsWith('https://res.cloudinary.com/')) return 'La fotografía debe ser una URL válida de Cloudinary.';
  if (socialLinks && !Array.isArray(socialLinks)) return 'Las redes sociales deben ser un arreglo.';
  return null;
};

exports.validateExperience = (fields) => {
  const { title, description, date, location, capacity, price, duration, category, images, status, menu } = fields;
  if (!title || title.length < 3) return 'El nombre de la experiencia es obligatorio y debe tener al menos 3 caracteres.';
  if (!description || description.length < 20) return 'La descripción es obligatoria y debe tener al menos 20 caracteres.';
  if (!date || isNaN(Date.parse(date)) /*|| new Date(date) < new Date()*/) return 'La fecha y hora son obligatorias y no pueden ser pasadas.';
  if (!location || !/^https?:\/\/.+/.test(location)) return 'La ubicación debe ser un enlace válido.';
  if (!capacity || !Number.isInteger(capacity) || capacity <= 0) return 'La capacidad máxima debe ser un número entero mayor a cero.';
  if (!price || isNaN(price) || Number(price) <= 0) return 'El precio por persona debe ser mayor a cero.';
  if (!duration || isNaN(duration) || Number(duration) <= 0) return 'La duración debe ser mayor a cero.';
  if (!category) return 'La categoría es obligatoria.';
  if (!images || !Array.isArray(images) || images.length === 0) return 'Debes subir al menos una imagen representativa.';
  if (!['Activa', 'Agotada', 'Próximamente'].includes(status)) return 'El estado debe ser Activa, Agotada o Próximamente.';
  // Validación de menú: al menos uno de los dos debe estar presente
  if (!menu || (!menu.image && !menu.text)) return 'Debes incluir el menú como imagen o texto.';
  if (menu.image && !menu.image.startsWith('https://res.cloudinary.com/')) return 'La imagen del menú debe ser una URL válida de Cloudinary.';
  if (menu.text && menu.text.length < 10) return 'El texto del menú debe tener al menos 10 caracteres.';
  return null;
};

exports.validateExperienceEdit = ({ location, date, status, capacity, price }) => {
  if (location !== undefined && !/^https?:\/\/.+/.test(location)) return 'La ubicación debe ser un enlace válido.';
  if (date !== undefined && (isNaN(Date.parse(date)) || new Date(date) < new Date())) return 'La fecha y hora no pueden ser pasadas.';
  if (capacity !== undefined && (!Number.isInteger(capacity) || capacity <= 0)) return 'La capacidad máxima debe ser un número entero mayor a cero.';
  if (price !== undefined && (isNaN(price) || Number(price) <= 0)) return 'El precio por persona debe ser mayor a cero.';
  if (status !== undefined && !['Activa', 'Agotada', 'Próximamente'].includes(status)) return 'El estado debe ser Activa, Agotada o Próximamente.';
  return null;
};