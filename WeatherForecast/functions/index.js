const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
const axios = require('axios');

admin.initializeApp();

// Configure Nodemailer
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'tempahoru@gmail.com',
        pass: 'karunya@7'
    }
});

exports.sendWeatherUpdates = functions.https.onCall(async (data) => {
    const { latitude, longitude } = data;
    const weatherDetails = await fetchWeatherDetails(latitude, longitude);

    // Recipient details
    const emailRecipient = 'keerthivaasankarthi@gmail.com';
    const smsRecipient = '6381112653'; // Replace with actual phone number
    const smsGateway = 'jio.com'; // Jio's SMS gateway domain

    // Send Email using Nodemailer
    const mailOptions = {
        from: 'tempahoru@gmail.com',
        to: emailRecipient,
        subject: 'Weather Update',
        text: weatherDetails
    };

    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            return console.log(error);
        }
        console.log('Email sent: ' + info.response);
    });

    // Send SMS using Email-to-SMS Gateway
    const smsOptions = {
        from: 'tempahoru@gmail.com',
        to: `${smsRecipient}@${smsGateway}`,
        subject: '', // Subject is typically ignored in SMS
        text: weatherDetails
    };

    transporter.sendMail(smsOptions, (error, info) => {
        if (error) {
            return console.log(error);
        }
        console.log('SMS sent: ' + info.response);
    });

    return { success: true };
});

async function fetchWeatherDetails(latitude, longitude) {
    const apiKey = 'caa95405da3c122ede62d8692dc4c65e';
    const url = `https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=metric&appid=${apiKey}`;

    try {
        const response = await axios.get(url);
        const weather = response.data.weather[0].description;
        const temp = response.data.main.temp;
        return `Today's weather is ${weather} with a high of ${temp}°C.`;
    } catch (error) {
        console.error('Error fetching weather data:', error);
        return 'Could not retrieve weather data at this time.';
    }
}
