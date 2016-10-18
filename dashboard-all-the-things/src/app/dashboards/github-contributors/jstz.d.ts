declare module 'jstz' {
  export function determine(): Timezone;
}

declare interface Timezone {
  name(): string;
}
