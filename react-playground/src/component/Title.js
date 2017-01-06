import React, { PropTypes } from 'react'

const Title = ({ text }) => {
  return (
    <h1>{text}</h1>
  );
};

Title.propTypes = {
  text: PropTypes.string.isRequired
};

export default Title;
