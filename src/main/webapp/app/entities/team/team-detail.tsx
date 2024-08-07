import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './team.reducer';

export const TeamDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const teamEntity = useAppSelector(state => state.team.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="teamDetailsHeading">Team</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{teamEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{teamEntity.name}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{teamEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>{teamEntity.createdDate ? <TextFormat value={teamEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{teamEntity.createdBy}</dd>
          <dt>
            <span id="modifiedDate">Modified Date</span>
          </dt>
          <dd>{teamEntity.modifiedDate ? <TextFormat value={teamEntity.modifiedDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="modifiedBy">Modified By</span>
          </dt>
          <dd>{teamEntity.modifiedBy}</dd>
          <dt>Team Lead</dt>
          <dd>{teamEntity.teamLead ? teamEntity.teamLead.id : ''}</dd>
          <dt>Team Members</dt>
          <dd>
            {teamEntity.teamMembers
              ? teamEntity.teamMembers.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {teamEntity.teamMembers && i === teamEntity.teamMembers.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/team" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/team/${teamEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TeamDetail;
