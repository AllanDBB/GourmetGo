const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const validateUser = require('../middlewares/validateUser');
const validateChef = require('../middlewares/validateChef');

// User registration
router.post('/register', validateUser, authController.registerUser);
router.post('/register-chef', validateChef, authController.registerChef);

// Login 
router.post('/login', authController.login);

// Logout
router.post('/logout', authController.logout);

// Refresh token 
router.post('/refresh', authController.refresh);

// Pass recover
router.post('/recover', authController.passwordRecovery);

// Password reset
router.post('/reset-password', authController.resetPassword);

module.exports = router;