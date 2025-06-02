const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();


const app = express();

// Cors middleware
app.use(cors({
  origin: process.env.CORS_ORIGIN || 'http://localhost:3000',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  credentials: true,
}));

// Middleware to parse JSON requests
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Import routers
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const chefRoutes = require('./routes/chefRoutes'); 
const experienceRoutes = require('./routes/experienceRoutes');
const bookingRoutes = require('./routes/bookingRoutes');
const ratingRoutes = require('./routes/ratingRoutes');

app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/chefs', chefRoutes);
app.use('/api/experiences', experienceRoutes);
app.use('/api/bookings', bookingRoutes);
app.use('/api/ratings', ratingRoutes);

// Ping endpoint
app.get('/ping', (req, res) => {
  res.status(200).json({ message: 'pong' });
});


// Middleware to serve static files (if needed)
app.use(express.static('public'));

// Middleware to handle errors
const errorHandler = require('./middlewares/errorHandler');
const notFound = require('./middlewares/notFound');

app.use(errorHandler);
app.use(notFound);

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
})
.then(() => {
  console.log('Connected to MongoDB');
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`Server listing at ${PORT}`);
  });
})
.catch(err => {
  console.error('Error conneting to MongoDB:', err);
});
