import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ITeam } from 'app/shared/model/team.model';
import { getEntities as getTeams } from 'app/entities/team/team.reducer';
import { IMessage } from 'app/shared/model/message.model';
import { getEntity, updateEntity, createEntity, reset } from './message.reducer';

export const MessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const teams = useAppSelector(state => state.team.entities);
  const messageEntity = useAppSelector(state => state.message.entity);
  const loading = useAppSelector(state => state.message.loading);
  const updating = useAppSelector(state => state.message.updating);
  const updateSuccess = useAppSelector(state => state.message.updateSuccess);

  const handleClose = () => {
    navigate('/message' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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
    values.dateSent = convertDateTimeToServer(values.dateSent);

    const entity = {
      ...messageEntity,
      ...values,
      toUserId: users.find(it => it.id.toString() === values.toUserId.toString()),
      fromUserId: users.find(it => it.id.toString() === values.fromUserId.toString()),
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
          dateSent: displayDefaultDateTime(),
        }
      : {
          ...messageEntity,
          dateSent: convertDateTimeFromServer(messageEntity.dateSent),
          toUserId: messageEntity?.toUserId?.id,
          fromUserId: messageEntity?.fromUserId?.id,
          team: messageEntity?.team?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="trackerApp.message.home.createOrEditLabel" data-cy="MessageCreateUpdateHeading">
            Create or edit a Message
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="message-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Email" id="message-email" name="email" data-cy="email" type="text" />
              <ValidatedField
                label="Message"
                id="message-message"
                name="message"
                data-cy="message"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Send To All Team Members"
                id="message-sendToAllTeamMembers"
                name="sendToAllTeamMembers"
                data-cy="sendToAllTeamMembers"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Date Sent"
                id="message-dateSent"
                name="dateSent"
                data-cy="dateSent"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="message-toUserId" name="toUserId" data-cy="toUserId" label="To User Id" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="message-fromUserId" name="fromUserId" data-cy="fromUserId" label="From User Id" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="message-team" name="team" data-cy="team" label="Team" type="select">
                <option value="" key="0" />
                {teams
                  ? teams.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/message" replace color="info">
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

export default MessageUpdate;
