import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './template-checklist.reducer';

export const TemplateChecklistDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const templateChecklistEntity = useAppSelector(state => state.templateChecklist.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="templateChecklistDetailsHeading">Template Checklist</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{templateChecklistEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{templateChecklistEntity.name}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {templateChecklistEntity.createdDate ? (
              <TextFormat value={templateChecklistEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{templateChecklistEntity.createdBy}</dd>
          <dt>
            <span id="modifiedDate">Modified Date</span>
          </dt>
          <dd>
            {templateChecklistEntity.modifiedDate ? (
              <TextFormat value={templateChecklistEntity.modifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="modifiedBy">Modified By</span>
          </dt>
          <dd>{templateChecklistEntity.modifiedBy}</dd>
          <dt>Task</dt>
          <dd>{templateChecklistEntity.task ? templateChecklistEntity.task.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/template-checklist" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/template-checklist/${templateChecklistEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TemplateChecklistDetail;
