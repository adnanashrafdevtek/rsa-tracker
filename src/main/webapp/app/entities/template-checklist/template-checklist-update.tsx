import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITemplateTask } from 'app/shared/model/template-task.model';
import { getEntities as getTemplateTasks } from 'app/entities/template-task/template-task.reducer';
import { ITemplateChecklist } from 'app/shared/model/template-checklist.model';
import { getEntity, updateEntity, createEntity, reset } from './template-checklist.reducer';

export const TemplateChecklistUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const templateTasks = useAppSelector(state => state.templateTask.entities);
  const templateChecklistEntity = useAppSelector(state => state.templateChecklist.entity);
  const loading = useAppSelector(state => state.templateChecklist.loading);
  const updating = useAppSelector(state => state.templateChecklist.updating);
  const updateSuccess = useAppSelector(state => state.templateChecklist.updateSuccess);

  const handleClose = () => {
    navigate('/template-checklist' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTemplateTasks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.modifiedDate = convertDateTimeToServer(values.modifiedDate);

    const entity = {
      ...templateChecklistEntity,
      ...values,
      task: templateTasks.find(it => it.id.toString() === values.task.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdDate: displayDefaultDateTime(),
          modifiedDate: displayDefaultDateTime(),
        }
      : {
          ...templateChecklistEntity,
          createdDate: convertDateTimeFromServer(templateChecklistEntity.createdDate),
          modifiedDate: convertDateTimeFromServer(templateChecklistEntity.modifiedDate),
          task: templateChecklistEntity?.task?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="trackerApp.templateChecklist.home.createOrEditLabel" data-cy="TemplateChecklistCreateUpdateHeading">
            Create or edit a Template Checklist
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="template-checklist-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Name"
                id="template-checklist-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Created Date"
                id="template-checklist-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Created By" id="template-checklist-createdBy" name="createdBy" data-cy="createdBy" type="text" />
              <ValidatedField
                label="Modified Date"
                id="template-checklist-modifiedDate"
                name="modifiedDate"
                data-cy="modifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Modified By" id="template-checklist-modifiedBy" name="modifiedBy" data-cy="modifiedBy" type="text" />
              <ValidatedField id="template-checklist-task" name="task" data-cy="task" label="Task" type="select">
                <option value="" key="0" />
                {templateTasks
                  ? templateTasks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/template-checklist" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TemplateChecklistUpdate;
