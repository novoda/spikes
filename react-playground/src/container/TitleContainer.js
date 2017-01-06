import { connect } from 'react-redux';
import Title from '../component/Title';

const mapStateToProps = (state, ownProps) => {
  return {
    text: state.titleText
  };
}

const TitleContainer = connect(
  mapStateToProps
)(Title);

export default TitleContainer;
