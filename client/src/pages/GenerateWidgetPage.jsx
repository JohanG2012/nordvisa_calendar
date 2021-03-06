import React, { Component } from 'react';
import PropTypes from 'prop-types';
import './GenerateWidgetPage.css';
import ErrorList from '../components/ErrorList';
import ProvinceSelect from '../components/ProvinceSelect';
import Client from '../Client';
import PageTitle from '../components/PageTitle';
import Button from '../components/Button';
import CountrySelect from '../components/CountrySelect';
import GeneratedOutput from '../components/GeneratedOutput';
import PageContainer from '../components/PageContainer';

const contextTypes = {
  language: PropTypes.object,
};

class GenerateWidgetPage extends Component {

  constructor() {
    super();

    this.onInputChange = this.onInputChange.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);
  }

  state = {
    fields: {
      region: '',
      province: '',
    },
    fieldErrors: [],
    showProvince: false,
    isGenerated: false,
    headCode: '',
    bodyCode: '',
    token: '',
  }


  componentDidMount() {
    const uri = '/api/token';
    Client.get(uri)
      .then((res) => {
        const token = res.token;
        this.setState({ token });
      });
  }

  onFormSubmit(event) {
    event.preventDefault();
    const fieldErrors = this.validate(this.state.fields);
    this.setState({ fieldErrors });

    // Return on Errors
    if (fieldErrors.length) return;

    const bodyCode = this.generateBodyCode(this.state.fields.region, this.state.fields.province);
    const headCode = `<script src="${location.protocol}//${location.host}/widget.js}></script>`;

    this.setState({ isGenerated: true });
    this.setState({ headCode });
    this.setState({ bodyCode });
  }

  onInputChange(event) {
    const fields = Object.assign({}, this.state.fields);
    fields[event.target.name] = event.target.value;
    this.setState({ fields });

    let showProvince;

    if (event.target.value !== '') {
      showProvince = true;
    } else {
      showProvince = false;
    }

    this.setState({ showProvince });
  }

  validate(fields) {
    const errors = [];
    if (!fields.region) errors.push(this.context.language.Errors.chooseRegion);
    return errors;
  }

  generateBodyCode(region, province) {
    return `<div id="visa-widget" data-country="${region}" data-region="${province}" data-token="${this.state.token}"></div>`;
  }

  render() {
    const language = this.context.language.WidgetView;

    return (
      <PageContainer size="small" className="widget-view">
        <PageTitle>{language.generateWidgetCode}</PageTitle>
        <form>
          <CountrySelect onInputChange={this.onInputChange} region={this.state.fields.region} />
          <ProvinceSelect
            region={this.state.fields.region}
            onChange={this.onInputChange}
            showProvince={this.state.showProvince}
          />
          <ErrorList errors={this.state.fieldErrors} />
          <Button onClick={this.onFormSubmit}>{language.generate}</Button>
        </form>
        <GeneratedOutput
          isGenerated={this.state.isGenerated}
          headCode={this.state.headCode}
          bodyCode={this.state.bodyCode}
        />
      </PageContainer>
    );
  }
}

GenerateWidgetPage.contextTypes = contextTypes;

export default GenerateWidgetPage;
