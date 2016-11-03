/* tslint:disable:no-unused-variable */

import { addProviders, inject } from '@angular/core/testing';
import { SonarService } from './sonar.service';
import { Http, BaseRequestOptions, ResponseOptions, Response } from '@angular/http';
import { MockBackend } from '@angular/http/testing/mock_backend';
import { ConfigService } from '../../config.service';
import { Observable } from 'rxjs';
import { SonarDetails } from './SonarDetails';

describe('Service: Sonar', () => {

  let mockBackend: MockBackend;
  let configService: ConfigService;
  let sonarService: SonarService;

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
      SonarService
    ]);
  });

  beforeEach(inject([MockBackend, ConfigService, SonarService],
    (_mockBackend_: MockBackend, _configService_: ConfigService, _sonarService_: SonarService) => {
      mockBackend = _mockBackend_;
      configService = _configService_;
      sonarService = _sonarService_;
    }
  ));

  beforeEach(() => {
    spyOn(configService, 'getSonarDetails').and.returnValue(Observable.from([new SonarDetails('https://your-website.com/api', 'token')]));
  });

  it('instantiates the sonar service', () => {
    expect(sonarService).toBeTruthy();
  });

  // it('converts the returned data into a JSON object', () => {
  //   let response = new Response(new ResponseOptions({ body: '{"some": "string", "one": 1}' }));
  //   mockBackend.connections.subscribe(connection => connection.mockRespond(response));

  //   sonarService.getAggregatedStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
  //     .subscribe(value => {
  //       expect(value).toEqual({
  //         some: 'string',
  //         one: 1
  //       });
  //     });
  // });

});
