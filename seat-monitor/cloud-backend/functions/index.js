const functions = require('firebase-functions');
const admin = require('firebase-admin');
const BigQuery = require('@google-cloud/bigquery');
const cors = require('cors')({ origin: true });

admin.initializeApp(functions.config().firebase);

// TODO: Make sure you set the `bigquery.projectid` Google Cloud environment variable.
const projectId = functions.config().bigquery.projectid;
// TODO: Make sure you set the `bigquery.datasetname` Google Cloud environment variable.
const datasetName = functions.config().bigquery.datasetname;
// TODO: Make sure you set the `bigquery.tablename` Google Cloud environment variable.
const tableName = functions.config().bigquery.tablename;

const db = admin.database();

const bigquery = BigQuery({
  projectId: projectId
});

/**
 * Receive data from pubsub, then 
 * Write telemetry raw data to bigquery
 * Maintain last data on firebase realtime database
 */
exports.receiveTelemetry = functions.pubsub
  .topic('measurements')
  .onPublish(event => {
    const attributes = event.data.attributes;
    const message = event.data.json;

    const deviceId = attributes['deviceId'];

    const data = {
      deviceId: deviceId,
      timestamp: event.timestamp,
      office: message.office,
      location: message.location,
      seatdata: message.dataLoadGauges
    };

    // validate input here if required (for example rogue locations)

    return Promise.all([
      insertIntoBigquery(data),
      updateCurrentDataFirebase(data)
    ]);
  });

/**
 * Store all the raw data in bigquery
 */
function insertIntoBigquery(data) {

	var rows = []

	data.seatdata.forEach(function(seat) {

		const row = {
			deviceId: data.deviceId,
	      	timestamp: data.timestamp,
	      	office: data.office,
	      	location: data.location,
	      	sensorId: seat.id,
	      	weight: seat.weight
		}
		rows.push(row);

	})

	bigquery
	  .dataset(datasetName)
	  .table(tableName)
	  .insert(rows)
	  .then((response) => {
	  	const insertErrors = response.insertErrors;
	    if (insertErrors && insertErrors.length > 0) {
	      console.log('start insert errors');
	      insertErrors.forEach((err) => console.error(JSON.stringify(err, null, 2)));
	      console.log('end insert errors');
	    } else {
	      console.log('Inserted:', rows);	
	    }
	  })
	  .catch((err) => {
	    console.error('Exception:', JSON.stringify(err, null, 2));
	  });
}

/** 
 * Maintain last status in firebase
*/
function updateCurrentDataFirebase(data) {
  return db.ref(`/devices/${data.deviceId}`).set({
    office: data.office,
    location: data.location,
    seatData: data.seatdata,
    lastTimestamp: data.timestamp
  });
}