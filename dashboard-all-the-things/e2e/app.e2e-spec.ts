import { WebDashboardPage } from './app.po';

describe('web-dashboard App', function() {
  let page: WebDashboardPage;

  beforeEach(() => {
    page = new WebDashboardPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
