/* tslint:disable:no-unused-variable */

import { addProviders, inject } from '@angular/core/testing';
import { ReportsMockService } from './reports-mock.service';
import { ReportsService } from './reports.service';
import { async } from '@angular/core/testing/async';

describe('Service: ReportsMockService', () => {

  let service: ReportsMockService;

  beforeEach(() => {
    addProviders([
      {
        provide: ReportsService,
        useClass: ReportsMockService
      }
    ]);
  });

  beforeEach(inject([ReportsService], (_service_: ReportsMockService) => {
    service = _service_;
  }));

  it('should instantiate the mock service', () => {
    expect(service).toBeTruthy();
  });

  it('should return an Observable containing only 1 element', async(() => {
    let values: any[] = new Array(0);

    service.getAggregatedStats()
      .subscribe(
        (value: any) => {
          values.push(value);
        },
        (error: any) => {
          fail('No error should be thrown from the mock service.');
        },
        () => {
          expect(values.length).toBe(1);
        }
      );
  }));

});
