import React, { PropTypes } from 'react';
import Film from './Film';

class Films extends React.Component {

  componentDidMount() {
    this.props.fetchData();
  }

  render() {
    const list = this.props.films.map(film => {
      return (
        <li key={film.title}><Film film={film}/></li>
      );
    });

    const style = {
      listStyle: 'none'
    };

    return (
      <ul style={style}>{list}</ul>
    );
  }

}

export default Films;
