import React, { FunctionComponent, useState } from 'react';
import { Container, Left, Right, SaveButton } from './Card.styles';

interface ICard {
  text: string;
  id: string;
  cardsDispatch: any;
}

const Card: FunctionComponent<ICard> = ({ text, id, cardsDispatch }) => {
  const [isEdit, setIsEdit] = useState(false);
  const onDeleteClick = () => {
    cardsDispatch({ type: 'REMOVE', payload: { id } });
  };

  const onEditClick = (evt: any, id: string) => {
    setIsEdit(true);
  };

  const handleNameChange = (evt: any) => {
    const { value } = evt.target;
    cardsDispatch({
      type: 'EDIT',
      payload: { id, editValue: value },
    });
  };

  return (
    <Container>
      <Left>
        {isEdit ? (
          <input
            type="text"
            defaultValue={text}
            onChange={handleNameChange}
            onBlur={() => setIsEdit(false)}
            onKeyPress={evt => {
              if (evt.key === 'Enter') {
                setIsEdit(false);
              }
            }}
          />
        ) : (
          text
        )}
      </Left>
      <Right>
        {isEdit ? (
          <SaveButton onClick={() => setIsEdit(false)}>Save</SaveButton>
        ) : (
          <>
            <button onClick={evt => onEditClick(evt, id)}>✎</button>
            <button onClick={onDeleteClick}>&times;</button>
          </>
        )}
      </Right>
    </Container>
  );
};

export default Card;
