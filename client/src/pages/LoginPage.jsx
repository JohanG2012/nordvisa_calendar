import React, { Component } from 'react';
import Link from 'react-router/Link';
import PropTypes from 'prop-types';
import { isEmail } from 'validator';
import Redirect from 'react-router/Redirect';
import './LoginPage.css';
import Client from '../Client';
import Loader from '../components/Loader';
import PageTitle from '../components/PageTitle';
import PageContainer from '../components/PageContainer';
import LoginForm from '../components/LoginForm';

const contextTypes = {
  language: PropTypes.object,
};

const propTypes = {
  location: PropTypes.shape({
    state: PropTypes.string,
  }).isRequired,
};

class LoginPage extends Component {
  constructor() {
    super();

    this.onFormSubmit = this.onFormSubmit.bind(this);
    this.onInputChange = this.onInputChange.bind(this);
  }
  state = {
    fields: {
      email: '',
      password: '',

    },
    fieldErrors: [],
    loginInProgress: false,
    shouldRedirect: false,
  }


  componentWillMount() {
    const fieldErrors = [...this.state.fieldErrors];

    if (this.props.location.state) {
      if (this.props.location.state.referrer === '/register') {
        fieldErrors.push(this.context.language.LoginView.registration);
      }
      if (this.props.location.state.referrer === '/recover-password') {
        fieldErrors.push(this.context.language.LoginView.recover);
      }
    }

    this.setState(fieldErrors);
  }


  onFormSubmit(event) {
    event.preventDefault();

    const fieldErrors = this.validate(this.state.fields);
    this.setState({ fieldErrors });
    if (fieldErrors.length) return;

    const user = {
      username: this.state.fields.email,
      password: this.state.fields.password,
    };

    this.performLogin(user);
    this.setState({ fields: {} });
  }

  onInputChange(event) {
    const fields = Object.assign({}, this.state.fields);
    fields[event.target.name] = event.target.value;
    this.setState({ fields });
  }

  validate(fields) {
    const errors = [];
    if (!isEmail(fields.email)) errors.push(this.context.language.Errors.invalidEmail);
    if (!fields.password) errors.push(this.context.language.Errors.emptyPassword);
    if (fields.password.length < 10) errors.push(this.context.language.Errors.incorrectPassword);
    if (fields.password.length > 255) errors.push(this.context.language.Errors.incorrectPassword);
    return errors;
  }

  performLogin(user) {
    this.setState({ loginInProgress: true });
    const uri = '/login';
    Client.login(user, uri).then((res) => {
      if (res === 'success') {
        this.setState({ shouldRedirect: true });
        this.forceUpdate();
      } else {
        const fieldErrors = [this.context.language.Errors.loginFailed];
        this.setState({ loginInProgress: false, fieldErrors });
      }
    });
  }

  render() {
    const language = this.context.language;

    if (this.state.shouldRedirect) {
      return (
        <Redirect to="/user/event" />
      );
    } else if (this.state.loginInProgress) {
      return (
        <Loader />
      );
    }
    return (
      <PageContainer size="small" className="login">
        <PageTitle>{language.LoginView.login}</PageTitle>
        <LoginForm
          onFormSubmit={this.onFormSubmit}
          onInputChange={this.onInputChange}
          fields={this.state.fields}
          fieldErrors={this.state.fieldErrors}
        />
        <Link to="/recover-password" style={{ textTransform: 'capitalize' }}>{language.LoginView.forgot}</Link>
      </PageContainer>
    );
  }
}

LoginPage.propTypes = propTypes;
LoginPage.contextTypes = contextTypes;

export default LoginPage;
