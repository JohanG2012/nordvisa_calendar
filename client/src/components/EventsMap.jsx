import React from 'react';
import GoogleMapReact from 'google-map-react';
import PropTypes from 'prop-types';
import MapMarker from './MapMarker';

const EventsMap = props => (
  <GoogleMapReact
    defaultCenter={props.center}
    defaultZoom={props.zoom}
    bootstrapURLKeys={{
      key: 'AIzaSyDIKbno4BEY3C76YjjHOvYfDc1kYEAYd4w',
      language: 'en',
    }}
  >

    {
        props.events.map(event => (
          <MapMarker
            lat={event.location.coordinates.coordinates[1]}
            lng={event.location.coordinates.coordinates[0]}
          />
      ))
      }
  </GoogleMapReact>
);

EventsMap.defaultProps = {
  center: { lat: 59.329323, lng: 18.068581 },
  zoom: 11,
};

EventsMap.propTypes = {
  center: PropTypes.shape({
    lat: PropTypes.number,
    lng: PropTypes.number,
  }),
  zoom: PropTypes.number,
  events: PropTypes.arrayOf(
    PropTypes.object,
  ).isRequired,
};

export default EventsMap;
