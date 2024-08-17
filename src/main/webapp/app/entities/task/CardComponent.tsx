// CardComponent.js

import React from 'react';
import { Col, Form, Row, Card, CardBody, CardTitle, CardSubtitle, CardText } from 'reactstrap';
// import counter from './'; // Import the icon
import './styles.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const MyCard = ({ team, title, assignedTo, priority, dueDate, color }) => {
  return (
    // <div  style={cardStyle}>

    <div className="cardKanban">
      <div className="d-flex bd-highlight mb-3">
        <div className="me-auto p-2 ">{title}</div>
      </div>
      <div style={{ borderTop: '1px solid #e0e1dd ', marginLeft: 10, marginRight: 10 }}></div>
      <div className="d-flex bd-highlight mb-3"></div>
      <div className="d-flex bd-highlight mb-3">
        <div className=" p-2 bd-highlight">Team:{team}</div>
        <div className="cardKanbanPriority p-2 bd-highlight">Priority:{priority}</div>
      </div>
      <div style={{ borderTop: '1px solid #e0e1dd ', marginLeft: 10, marginRight: 10 }}></div>
      <div className="d-flex bd-highlight mb-3">
        {/* <div className="p-2 bd-highlight">Due Date:01/2/2035</div> */}
        <FontAwesomeIcon icon="user" />
        <div className="me-auto p-2 bd-highlight">{assignedTo}</div>

        <div className="cardKanbanView ms-auto">View</div>
      </div>
    </div>
  );
};

export default MyCard;
