import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IMessage } from 'app/shared/model/message.model';
import { getEntities as getMessages } from 'app/entities/message/message.reducer';
import { ITeam } from 'app/shared/model/team.model';
import { getEntities as getTeams } from 'app/entities/team/team.reducer';
import { IDocument } from 'app/shared/model/document.model';
import { getEntity, updateEntity, createEntity, reset } from './document.reducer';

export const DocumentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const messages = useAppSelector(state => state.message.entities);
  const teams = useAppSelector(state => state.team.entities);
  const documentEntity = useAppSelector(state => state.document.entity);
  const loading = useAppSelector(state => state.document.loading);
  const updating = useAppSelector(state => state.document.updating);
  const updateSuccess = useAppSelector(state => state.document.updateSuccess);

  const handleClose = () => {
    navigate('/document' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMessages({}));
    dispatch(getTeams({}));
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

    const entity = {
      ...documentEntity,
      ...values,
      message: messages.find(it => it.id.toString() === values.message.toString()),
      team: teams.find(it => it.id.toString() === values.team.toString()),
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
        }
      : {
          ...documentEntity,
          createdDate: convertDateTimeFromServer(documentEntity.createdDate),
          message: documentEntity?.message?.id,
          team: documentEntity?.team?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="trackerApp.document.home.createOrEditLabel" data-cy="DocumentCreateUpdateHeading">
            Create or edit a Document
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="document-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="document-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Mime Type"
                id="document-mimeType"
                name="mimeType"
                data-cy="mimeType"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Url" id="document-url" name="url" data-cy="url" type="text" />
              <ValidatedField
                label="Created Date"
                id="document-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Created By" id="document-createdBy" name="createdBy" data-cy="createdBy" type="text" />
              <ValidatedField id="document-message" name="message" data-cy="message" label="Message" type="select">
                <option value="" key="0" />
                {messages
                  ? messages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="document-team" name="team" data-cy="team" label="Team" type="select">
                <option value="" key="0" />
                {teams
                  ? teams.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/document" replace color="info">
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

export default DocumentUpdate;
