import React, { Component } from "react";
import Client from "../Client";
import Link from "react-router/Link";
import PropTypes from "prop-types";

class MenuList extends Component {
  render() {

    const language = this.context.language.MenuList;

    return (
      <ul>
        {
          Client.getUserLevel() > 1 ? (
            <div>
              <li><Link to="/admin/members" className="menu-link">Members</Link></li>
              <li><Link to="/admin/pending-registrations" className="menu-link">Pending Registrations</Link></li>
            </div>
          ) : (
            <div></div>
          )
        }
        {
          Client.isLogedIn() ? (
            <div>
              <li><Link to="/user/account" className="menu-link">My Account</Link></li>
              <li><Link to="/user/event/create" className="menu-link">Create Event</Link></li>
              <li><Link to="/user/event" className="menu-link">My Events</Link></li>
              <li><Link to="/logout" className="menu-link">Logout</Link></li>
            </div>
          ) : (
            <div>
              <li><Link to="/login" className="menu-link capitalize">{language.login}</Link></li>
              <li><Link to="/register" className="menu-link capitalize">{language.register}</Link></li>
              <li><Link to="/recover-password" className="menu-link capitalize">{language.forgot}</Link></li>
            </div>
          )
        }
        <li><Link to="/generate-widget" className="menu-link capitalize">{language.generate}</Link></li>
      </ul>
    )
  }
}

MenuList.contextTypes = {
  language: PropTypes.object,
}

export default MenuList;
