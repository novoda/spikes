const BigQuery = require('@google-cloud/bigquery');
const cors = require('cors')({ origin: true });

const projectId = "seat-monitor"

const bigquery = BigQuery({
  projectId: projectId
});

/**
 * Responds to any HTTP request that can provide a "message" field in the body.
 *
 * @param {!Object} req Cloud Function request context.
 * @param {!Object} res Cloud Function response context.
 */
exports.heatMap = function heatMap(req, res) {
  const table = '`seat-monitor.seat_monitor_iot.raw_data`';

  // get the office and location from the GET request
  const office = req.query.office || "Liverpool";
  const location = req.query.location || "Downstairs";

  // use the office and location to find all sensor ids
  // use a sensor id to get all data for that sensor
  // average the weight column
  // limit to 1 result per sensor id
  const idQuery = `
      SELECT DISTINCT sensorId
      FROM ${table}
      WHERE (timestamp BETWEEN TIMESTAMP_SUB(CURRENT_TIMESTAMP(), INTERVAL 168 HOUR) AND CURRENT_TIMESTAMP()
            AND office = '${office}'
            AND location = '${location}')
    `;

  const query = `
    SELECT 
      TIMESTAMP_TRUNC(timestamp, HOUR, 'Europe/London') as data_hour,
      sensorId,
      avg(weight) as avg_weight,
      count(*) as data_points      
    FROM ${table}
    WHERE (timestamp BETWEEN TIMESTAMP_SUB(CURRENT_TIMESTAMP(), INTERVAL 168 HOUR) AND CURRENT_TIMESTAMP()
          AND office = '${office}'
          AND location = '${location}'
          AND sensorId IN (${idQuery}))
    group by data_hour, sensorId
    order by data_hour
  `;

  return bigquery
    .query({
      query: query,
      useLegacySql: false
    })
    .then(result => {
    	console.log("Result! " + JSON.stringify(result, null, 2));

// we now have an average weight for the timeframe per sensor id

// convert the average weights onto a scale of 0 - 100 (somehow)
// choice of relative scale to each other, or relative scale to a set range

      const singularMin = 0;
      const singularMax = 1000;

      var rows = []
      result[0].forEach(function(sensorResult) {
        const allTimeMin = singularMin * sensorResult.data_points;
        const allTimeMax = singularMax * sensorResult.data_points;
        const avgWeight = sensorResult.avg_weight;
        const result = (avgWeight / (allTimeMax - allTimeMin)) * 100;
        const sensorId = sensorResult.sensorId;
        const json = { sensorId: sensorId, heat: result }
        rows.push(json);
      });

      cors(req, res, () => {
        res.json(rows);
      });
    });
};
