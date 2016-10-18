import { ReportsService } from './reports.service';
import { Http } from '@angular/http';
import { environment } from '../../../environments/environment';
import { ReportsMockService } from './reports-mock.service';
import { ConfigService } from '../../../config.service';

let reportsServiceFactory = (http: Http, configService: ConfigService): any => {
  if (environment.mockReports) {
    return new ReportsMockService();
  }
  return new ReportsService(http, configService);
};

export const reportsServiceProvider = {
  provide: ReportsService,
  useFactory: reportsServiceFactory,
  deps: [Http, ConfigService]
};
