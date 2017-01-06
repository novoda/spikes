export const ON_DATA = 'on_data';

export function onData(data) {
  return {
    type: ON_DATA,
    data: data
  };
}
