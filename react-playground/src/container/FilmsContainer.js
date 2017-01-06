import { connect } from 'react-redux';
import Films from '../component/Films';
import onData from '../actions/actions';

const mapStateToProps = (state, ownProps) => {
  return {
    films: state.films
  };
};

const data = [
  {
    title: 'Rogue One',
    image: 'https://images-na.ssl-images-amazon.com/images/M/MV5BMjEwMzMxODIzOV5BMl5BanBnXkFtZTgwNzg3OTAzMDI@._V1_SY1000_SX675_AL_.jpg'
  },
  {
    title: 'Passengers',
    image: 'https://images-na.ssl-images-amazon.com/images/M/MV5BMTk4MjU3MDIzOF5BMl5BanBnXkFtZTgwMjM2MzY2MDI@._V1_SY1000_CR0,0,675,1000_AL_.jpg'
  }
];

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    fetchData: () => {
      dispatch(onData(data));
    }
  };
};

const FilmsContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(Films);

export default FilmsContainer;
