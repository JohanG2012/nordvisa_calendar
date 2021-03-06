import React, { Component } from 'react';
import Redirect from 'react-router/Redirect';
import PropTypes from 'prop-types';
import { isEmail } from 'validator';
import './RegisterPage.css';
import Client from '../Client';
import Loader from '../components/Loader';
import PageTitle from '../components/PageTitle';
import PageContainer from '../components/PageContainer';
import RegisterForm from '../components/RegisterForm';

const contextTypes = {
  language: PropTypes.object,
};

class RegisterPage extends Component {
  constructor() {
    super();

    this.onInputChange = this.onInputChange.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);
    this.callback = this.callback.bind(this);
  }
  state = {
    fields: {
      email: '',
      password: '',
      confirmpassword: '',
      org: '',
      neworg: '',
      recaptcha: '',
    },
    organizations: [],
    fieldErrors: [],
    loading: false,
    redirect: false,
  }

  componentDidMount() {
    const uri = '/api/visitor/organizations';
    Client.get(uri)
      .then((organizations) => {
        if (organizations.length > 0) {
          this.setState({ organizations });
        }
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

    // Do registration
    this.setState({ loading: true });
    const uri = '/api/visitor/registration';
    const user = {
      email: this.state.fields.email,
      password: this.state.fields.password,
      passwordConfirmation: this.state.fields.confirmpassword,
      organization: this.state.fields.neworg || this.state.fields.org,
      gRecaptchaResponse: this.state.fields.recaptcha,
    };

    Client.post(user, uri).then((res) => {
      this.setState({ loading: false });
      if (Object.prototype.hasOwnProperty.call(res, 'message')) {
        fieldErrors.push(res.message);
        this.setState({ fieldErrors });
      } else {
        this.setState({ redirect: true });
        this.setState({ fields: {} });
      }
    });
  }

  callback(key) {
    const fields = Object.assign({}, this.state.fields, { recaptcha: key });
    this.setState({ fields });
  }

  validate(fields) {
    const errors = [];
    if (!fields.email) errors.push(this.context.language.Errors.emailRequired);
    if (!fields.password || !fields.confirmpassword) {
      errors.push(this.context.language.Errors.passwordRequired);
    }
    if (fields.password !== fields.confirmpassword) {
      errors.push(this.context.language.Errors.passwordDoesNotMatch);
    }
    if (!fields.recaptcha) errors.push(this.context.language.Errors.captchaFails);
    if (fields.password.length < 10) errors.push(this.context.language.Errors.shortPassword);
    if (fields.password.length > 255) errors.push(this.context.language.Errors.longPassword);
    if (!isEmail(fields.email)) errors.push(this.context.language.Errors.invalidEmail);
    return errors;
  }

  render() {
    const language = this.context.language.RegisterView;

    if (this.state.loading) {
      return (
        <Loader />
      );
    } else if (this.state.redirect) {
      return (
        <Redirect to={{
          pathname: '/login',
          state: { referrer: '/register' },
        }}
        />
      );
    }
    return (
      <PageContainer size="small" className="register">
        <PageTitle>{language.register}</PageTitle>
        <RegisterForm
          onFormSubmit={this.onFormSubmit}
          onInputChange={this.onInputChange}
          callback={this.callback}
          fields={this.state.fields}
          fieldErrors={this.state.fieldErrors}
          organizations={this.state.organizations}
        />
      </PageContainer>
    );
  }
}

RegisterPage.contextTypes = contextTypes;

export default RegisterPage;
