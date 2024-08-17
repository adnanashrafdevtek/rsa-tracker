import React from 'react';

import { NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo-jhipster.png" alt="Logo" />
  </div>
);

export const Brand = () => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    <span className="brand-title">Tracker</span>
    <span className="navbar-version">{VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`}</span>
  </NavbarBrand>
);

export const Home = () => (
  <NavItem>
    <NavLink tag={Link} to="/" className="d-flex align-items-center">
      <FontAwesomeIcon icon="home" />
      <span>Home</span>
    </NavLink>
  </NavItem>
);

export const Team = () => (
  <NavItem>
    <NavLink tag={Link} to="/team" className="d-flex align-items-center">
      <FontAwesomeIcon icon="users" />
      <span>Teams</span>
    </NavLink>
  </NavItem>
);

export const Task = () => (
  <NavItem>
    <NavLink tag={Link} to="/taskKanban" className="d-flex align-items-center">
      <FontAwesomeIcon icon="list-check" />
      <span>tasks by status</span>
    </NavLink>
  </NavItem>
);
export const TaskByTeam = () => (
  <NavItem>
    <NavLink tag={Link} to="/taskKanban" className="d-flex align-items-center">
      <FontAwesomeIcon icon="list-check" />
      <span>tasks by teams</span>
    </NavLink>
  </NavItem>
);
