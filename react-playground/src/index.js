import React from 'react';
import { render } from 'react-dom';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import App from './component/App';
import Reducer from './reducer';

const store = createStore(Reducer, { films: [] });

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('hello')
);

store.dispatch({type: 'hello'});
