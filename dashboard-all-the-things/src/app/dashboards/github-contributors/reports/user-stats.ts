import Any = jasmine.Any;
export class UserStats {

  constructor(public username: string|Any,
              public assignedProjects: string|Any,
              public assignedCount: number|Any,
              public externalCount: number|Any) {
    // noop
  }

}
