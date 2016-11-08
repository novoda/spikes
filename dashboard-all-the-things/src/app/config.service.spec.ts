/* tslint:disable:no-unused-variable */

import { addProviders, inject } from '@angular/core/testing';
import { ConfigService } from './config.service';
import { MockBackend } from '@angular/http/testing/mock_backend';
import { BaseRequestOptions, Http, ResponseOptions, Response } from '@angular/http';
import { async } from '@angular/core/testing/async';

describe('Service: Config', () => {

  let mockBackend: MockBackend;
  let configService: ConfigService;

  beforeEach(() => {
    addProviders([
      MockBackend,
      BaseRequestOptions,
      {
        provide: Http,
        useFactory: (backend, defaultOptions) => new Http(backend, defaultOptions),
        deps: [MockBackend, BaseRequestOptions]
      },
      ConfigService
    ]);
  });

  beforeEach(inject([MockBackend], (_mockBackend_) => {
    mockBackend = _mockBackend_;
  }));

  describe('with success backend', () => {

    beforeEach(() => {
      let response = new Response(new ResponseOptions({
        body: `{
        "api": "https://your-webservice.com/api/",
        "serverUrl": "https://your-webservice.com:[port]"        
      }`
      }));
      mockBackend.connections.subscribe(connection => connection.mockRespond(response));
    });

    beforeEach(inject([ConfigService], (_configService_) => {
      configService = _configService_;
    }));

    it('creates an instance of Config', () => {
      expect(configService).toBeTruthy();
    });

    it('executes the input handler with the right API base', async(() => {
      configService
        .getApiBase()
        .subscribe((apiBase: string) => {
          expect(apiBase).toBe('https://your-webservice.com/api/');
        });
    }));

    it('executes the input handler with the right server url', async(() => {
      configService
        .getServerUrl()
        .subscribe((serverUrl: string) => {
          expect(serverUrl).toBe('https://your-webservice.com:[port]');
        });
    }));

  });

  describe('with failing backend', () => {

    beforeEach(() => {
      mockBackend.connections.subscribe(connection => connection.mockError(new Error('Some network error')));
    });

    beforeEach(inject([ConfigService], (_configService_) => {
      configService = _configService_;
    }));

    it('executes the input handler with a null API base', () => {
      configService
        .getApiBase()
        .subscribe(null, (error: Error) => {
          expect(error.message).toContain('config.json');
        });
    });

  });

});
