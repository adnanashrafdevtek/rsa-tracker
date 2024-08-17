import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './template-task.reducer';

export const TemplateTaskDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const templateTaskEntity = useAppSelector(state => state.templateTask.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="templateTaskDetailsHeading">Template Task</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{templateTaskEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{templateTaskEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{templateTaskEntity.description}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {templateTaskEntity.createdDate ? (
              <TextFormat value={templateTaskEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{templateTaskEntity.createdBy}</dd>
          <dt>
            <span id="modifiedDate">Modified Date</span>
          </dt>
          <dd>
            {templateTaskEntity.modifiedDate ? (
              <TextFormat value={templateTaskEntity.modifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="modifiedBy">Modified By</span>
          </dt>
          <dd>{templateTaskEntity.modifiedBy}</dd>
          <dt>Template</dt>
          <dd>{templateTaskEntity.template ? templateTaskEntity.template.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/template-task" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/template-task/${templateTaskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TemplateTaskDetail;
