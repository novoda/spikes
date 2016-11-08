import { UserStats } from './user-stats';

export class CompanyStats {

  constructor(public contributors: Array<UserStats>,
              public slackers: Array<UserStats>) {
    // noop
  }

}
