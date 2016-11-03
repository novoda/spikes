/* tslint:disable:no-unused-variable */

import { addProviders, inject } from '@angular/core/testing';
import { ReportsService } from './reports.service';
import { Http, BaseRequestOptions, ResponseOptions, Response } from '@angular/http';
import { MockBackend } from '@angular/http/testing/mock_backend';
import { ConfigService } from '../config.service';
import { Observable } from 'rxjs';

describe('Service: Reports', () => {

  const ANY_DATE = new Date();
  const ANY_TIMEZONE = 'Europe/London';

  let mockBackend: MockBackend;
  let configService: ConfigService;
  let reportsService: ReportsService;

  beforeEach(() => {
    addProviders([
      MockBackend,
      BaseRequestOptions,
      {
        provide: Http,
        useFactory: (backend, defaultOptions) => new Http(backend, defaultOptions),
        deps: [MockBackend, BaseRequestOptions]
      },
      ConfigService,
      ReportsService
    ]);
  });

  beforeEach(inject([MockBackend, ConfigService, ReportsService],
    (_mockBackend_: MockBackend, _configService_: ConfigService, _reportsService_: ReportsService) => {
      mockBackend = _mockBackend_;
      configService = _configService_;
      reportsService = _reportsService_;
    }
  ));

  beforeEach(() => {
    spyOn(configService, 'getApiBase').and.returnValue(Observable.from(['https://your-website.com/api/']));
  });

  it('instantiates the reports service', () => {
    expect(reportsService).toBeTruthy();
  });

  it('converts the returned string into a JSON object', () => {
    let response = new Response(new ResponseOptions({body: '{"some": "string", "one": 1}'}));
    mockBackend.connections.subscribe(connection => connection.mockRespond(response));

    reportsService.getAggregatedStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe(value => {
        expect(value).toEqual({
          some: 'string',
          one: 1
        });
      });
  });

});
