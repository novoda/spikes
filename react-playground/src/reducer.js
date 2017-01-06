import { ON_DATA } from './actions/actions';

const Reducer = (state = [], action) => {
  console.log('action: ' + action.type);
  switch (action.type) {
    case 'hello':
      return Object.assign({}, state, {
        titleText: 'A title'
      });
    case ON_DATA:
      return Object.assign({}, state, {
        films: action.data
      });
    default:
        return state;
  };
}

export default Reducer;
