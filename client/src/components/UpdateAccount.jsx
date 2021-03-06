import React, { Component } from 'react';
import { isEmail } from 'validator';
import PropTypes from 'prop-types';
import ErrorList from './ErrorList';
import Client from '../Client';
import Button from './Button';
import SubTitle from './SubTitle';
import OrganizationSelect from './OrganizationSelect';

const contextTypes = {
  language: PropTypes.object,
};

class UpdateAccount extends Component {
  constructor() {
    super();

    this.onInputChange = this.onInputChange.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);
  }

  state = {
    fields: {
      id: null,
      email: '',
      org: '',
      neworg: '',
    },
    organizations: [],
    fieldErrors: [],
  }

  componentDidMount() {
    const uri = '/api/user/current';
    Client.get(uri)
      .then((user) => {
        const fields = Object.assign({}, this.state.fields, {
          id: user.id,
          email: user.email,
          org: user.organization,
        });
        this.setState({ fields });
      });

    const orgUri = '/api/visitor/organizations';
    Client.get(orgUri)
      .then((organizations) => {
        this.setState({ organizations });
      });
  }

  onInputChange(event) {
    const fields = Object.assign({}, this.state.fields);
    fields[event.target.name] = event.target.value;
    this.setState({ fields });
  }

  onFormSubmit(event) {
    event.preventDefault();
    const fieldErrors = this.validate(this.state.fields);
    this.setState({ fieldErrors });

    // Return on Errors
    if (fieldErrors.length) return;

    const uri = '/api/user/update_user_details';
    const user = {
      id: this.state.fields.id,
      email: this.state.fields.email,
      organization: this.state.fields.neworg || this.state.fields.org,
    };

    Client.post(user, uri);

    fieldErrors.push('Account updated!');
    this.setState({ fieldErrors });

    this.setState({ fields: {
      email: '',
      org: '',
      neworg: '',
    } });
  }

  validate(fields) {
    const errors = [];
    if (!fields.email) errors.push(this.context.language.Errors.emailRequired);
    if (!isEmail(fields.email)) errors.push(this.context.language.Errors.invalidEmail);
    return errors;
  }

  render() {
    const language = this.context.language.MyAccountView;
    const style = {
      border: '1px solid grey',
      maxWidth: '600px',
      borderRadius: '3px',
      margin: '0 auto 20px',
    };

    return (
      <div style={style}>
        <SubTitle>{language.updateDetails}</SubTitle>
        <form onSubmit={this.onFormSubmit}>
          <label htmlFor="email" style={{ textTransform: 'capitalize' }}>{language.email}:</label>
          <input type="text" name="email" value={this.state.fields.email} onChange={this.onInputChange} />
          <OrganizationSelect
            onInputChange={this.onInputChange}
            fields={this.state.fields}
            organizations={this.state.organizations}
          />
          <ErrorList errors={this.state.fieldErrors} />
          <Button form>{language.save}</Button>
        </form>
      </div>
    );
  }
}

UpdateAccount.contextTypes = contextTypes;

export default UpdateAccount;
