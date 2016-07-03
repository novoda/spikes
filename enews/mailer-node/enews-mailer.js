var nodemailer = require('nodemailer');
var EnewsFetcher = require('enews-fetcher');
var generateHtml = require('html-generator').generateHtml;
var enewsFetcher = new EnewsFetcher(process.env.token);
var moment = require('moment');

const SMTP_USER = process.env.SMTP_USER;
const SMTP_PASSWORD = process.env.SMTP_PASSWORD;
const SMTP_HOST = process.env.SMTP_HOST;
const RECIPIENTS = process.env.RECIPIENTS;

createEnewsHtml(function(html, plaintText) {
  sendMail(html, plaintText);
});

function createEnewsHtml(callback) {
  enewsFetcher.getLastSevenDays(function(eNews) {
    var html = generateHtml(eNews);
    callback(html, 'todo');
  });
}

function sendMail(contentHtml, plainText) {
  var smtpConfig = {
    host: SMTP_HOST,
    port: 465,
    secure: true,
    auth: {
        user: SMTP_USER,
        pass: SMTP_PASSWORD
    }
  };

  var transporter = nodemailer.createTransport(smtpConfig);
  var mailOptions = {
    from: '"Hal Berto 👥" <' + SMTP_USER + '>',
    to: RECIPIENTS,
    subject: '#enews ' + moment().format('YYYY-MM-DD') + ' ✔',
    html: contentHtml,
    text: plainText
  };

  transporter.sendMail(mailOptions, function(error, info){
    if(error){
        return console.log(error);
    }
    console.log('Message sent: ' + info.response);
  });
}
