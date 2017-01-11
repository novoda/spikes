const nodemailer = require('nodemailer');
const EnewsFetcher = require('enews-fetcher');
const generateHtml = require('html-generator').generateHtml;
const enewsFetcher = new EnewsFetcher(process.env.token);
const moment = require('moment');

const SMTP_USER = process.env.SMTP_USER;
const SMTP_PASSWORD = process.env.SMTP_PASSWORD;
const SMTP_HOST = process.env.SMTP_HOST;
const RECIPIENTS = process.env.RECIPIENTS;

createEnewsHtml(function(html, plainText) {
  sendMail(html, plainText);
});

function createEnewsHtml(callback) {
  enewsFetcher.getLastSevenDays().then(function(eNews) {
    const html = generateHtml(eNews);
    const plainText = generatePlainText(eNews);
    callback(html, plainText);
  });
}

function generatePlainText(eNews) {
  return eNews.map(each => {
    return `** ${each.title} \n${each.link} \n------------------------------`;
  }).join('\n');
}

function sendMail(contentHtml, plainText) {
  const smtpConfig = {
    host: SMTP_HOST,
    port: 465,
    secure: true,
    auth: {
        user: SMTP_USER,
        pass: SMTP_PASSWORD
    }
  };

  const transporter = nodemailer.createTransport(smtpConfig);
  const mailOptions = {
    from: '"enewsletter" <' + SMTP_USER + '>',
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
