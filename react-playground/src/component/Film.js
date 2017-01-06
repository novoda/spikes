import React, { PropTypes } from 'react';

const Film = (props) => {
  const film = props.film;
  return (
    <img src={film.image} height="300"></img>
  );
};

Film.propTypes = {
  title: PropTypes.string.isRequired,
  image: PropTypes.string.isRequired
};

export default Film;
