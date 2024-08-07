import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './message.reducer';

export const MessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const messageEntity = useAppSelector(state => state.message.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="messageDetailsHeading">Message</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{messageEntity.id}</dd>
          <dt>
            <span id="email">Email</span>
          </dt>
          <dd>{messageEntity.email}</dd>
          <dt>
            <span id="message">Message</span>
          </dt>
          <dd>{messageEntity.message}</dd>
          <dt>
            <span id="sendToAllTeamMembers">Send To All Team Members</span>
          </dt>
          <dd>{messageEntity.sendToAllTeamMembers ? 'true' : 'false'}</dd>
          <dt>
            <span id="dateSent">Date Sent</span>
          </dt>
          <dd>{messageEntity.dateSent ? <TextFormat value={messageEntity.dateSent} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>To User Id</dt>
          <dd>{messageEntity.toUserId ? messageEntity.toUserId.id : ''}</dd>
          <dt>From User Id</dt>
          <dd>{messageEntity.fromUserId ? messageEntity.fromUserId.id : ''}</dd>
          <dt>Team</dt>
          <dd>{messageEntity.team ? messageEntity.team.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/message/${messageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MessageDetail;
