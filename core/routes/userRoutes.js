const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const validateUserUpdate = require('../middlewares/validateUserUpdate');
const auth = require('../middlewares/auth');

router.put('/me', auth, validateUserUpdate, userController.updateMe);

router.get('/me', auth, userController.getMyProfile);

router.get('/:id', userController.getPublicProfile);

router.put('/change-password', auth, userController.changePassword);

module.exports = router;