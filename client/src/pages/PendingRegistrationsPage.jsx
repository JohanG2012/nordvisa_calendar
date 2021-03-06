import React, { Component } from 'react';
import PropTypes from 'prop-types';
import './PendingRegistrationsPage.css';
import RegistrationsList from '../components/RegistrationsList';
import Client from '../Client';
import PageTitle from '../components/PageTitle';
import Button from '../components/Button';
import PageContainer from '../components/PageContainer';

const contextTypes = {
  language: PropTypes.object,
};

class PendingRegistrationsPage extends Component {
  constructor() {
    super();

    this.onInputChange = this.onInputChange.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);
  }
  state = {
    registrations: [],
    approve: [],
    deny: [],
  }

  componentDidMount() {
    const uri = '/api/admin/registrations';
    Client.get(uri)
      .then((registrations) => {
        if (Array.isArray(registrations)) {
          this.setState({ registrations });
        }
      });
  }


  onInputChange(event) {
    let approve = [...this.state.approve];
    let deny = [...this.state.deny];

    // Is Approved
    if (event.target.value === 'approve') {
      // If it is in deny array, remove it
      if (deny.includes(event.target.name)) {
        deny = deny.filter(id => id !== event.target.name);
      }

      // Push user ID to approve array
      approve.push(event.target.name);

    // Is Denied
    } else {
      // If it is in approved array, remove it.
      if (approve.includes(event.target.name)) {
        approve = approve.filter(id => id !== event.target.name);
      }

      // Push user ID to deny array.
      deny.push(event.target.name);
    }

    this.setState({ approve });
    this.setState({ deny });
  }

  onFormSubmit(event) {
    event.preventDefault();
    const uri = '/api/admin/registrations';
    const approve = [...this.state.approve];
    const deny = [...this.state.deny];
    const oldRegistrations = [...this.state.registrations];

    // Remove registrations from users who have been denied or approved.
    const registrations = [];

    oldRegistrations.forEach((registration) => {
      let hasAction = false;
      approve.forEach((id) => {
        if (registration.id === id) {
          hasAction = true;
        }
      });

      deny.forEach((id) => {
        if (registration.id === id) {
          hasAction = true;
        }
      });

      if (!hasAction) {
        registrations.push(registration);
      }
    });


    // Empty approve array, and perform API action.
    while (approve.length > 0) {
      const obj = {
        id: approve.shift(),
        approved: true,
      };
      Client.post(obj, uri);
    }

    // Empty deny array, and perform API action.
    while (deny.length > 0) {
      const obj = {
        id: deny.shift(),
        approved: false,
      };
      Client.post(obj, uri);
    }

    this.setState({ registrations, deny, approve });
    this.forceUpdate();
  }

  render() {
    const language = this.context.language.PendingRegistrationsView;

    return (
      <PageContainer className="pending-view">
        <PageTitle>{language.pending}</PageTitle>
        <form onSubmit={this.onFormSubmit}>
          <RegistrationsList
            registrations={this.state.registrations}
            onInputChange={this.onInputChange}
          />
          <Button form>{language.approve}</Button>
        </form>
      </PageContainer>
    );
  }
}

PendingRegistrationsPage.contextTypes = contextTypes;

export default PendingRegistrationsPage;
