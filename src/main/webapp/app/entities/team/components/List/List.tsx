import React, { useState, FunctionComponent } from 'react';
import { Container, Header, CloseButton, Title, AddCardButton } from './List.styles';
import Card from '../Card/Card';
import { v1 as uuidv1 } from 'uuid';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import { IList, ICard } from '../../models';

interface IListProps {
  list: IList;
  cards: ICard[];
  cardsDispatch: any;
  listsDispatch: any;
}

const List: FunctionComponent<IListProps> = ({ list, cards, cardsDispatch, listsDispatch }) => {
  const [isEditingName, setEditingName] = useState(false);

  const getItemStyle = (isDragging: boolean, draggableStyle: any) => ({
    background: 'white',
    padding: '10px',
    marginBottom: '5px',
    borderRadius: '5px',
    borderBottom: '1px solid rgb(178,185,197)',

    ...draggableStyle,
  });

  const getListStyle = (isDraggingOver: boolean) => ({
    minHeight: 70,
  });

  const handleNameChange = (evt: any) => {
    const { value } = evt.target;
    listsDispatch({
      type: 'UPDATE_NAME',
      payload: { id: list.id, value },
    });
  };

  return (
    <Container>
      <Header>
        {isEditingName ? (
          <input
            type="text"
            defaultValue={list.listTitle}
            onChange={handleNameChange}
            onBlur={() => setEditingName(false)}
            onKeyPress={evt => {
              if (evt.key === 'Enter') {
                setEditingName(false);
              }
            }}
          />
        ) : (
          <Title onClick={() => setEditingName(true)}>{list.listTitle}</Title>
        )}
        <CloseButton
          onClick={() =>
            listsDispatch({
              type: 'REMOVE',
              payload: { id: list.id },
            })
          }
        >
          &times;
        </CloseButton>
      </Header>
      <Droppable droppableId={list.id}>
        {(provided, snapshot) => (
          <div {...provided.droppableProps} ref={provided.innerRef} style={getListStyle(snapshot.isDraggingOver)}>
            <>
              {cards.map((post: ICard, index: number) => (
                <Draggable key={post.id} index={index} draggableId={`draggable-${post.id}`}>
                  {(provided, snapshot) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                      {...provided.dragHandleProps}
                      style={getItemStyle(snapshot.isDragging, provided.draggableProps.style)}
                    >
                      <Card key={post.id} text={post.text} id={post.id} cardsDispatch={cardsDispatch} />
                    </div>
                  )}
                </Draggable>
              ))}
            </>

            {provided.placeholder}
          </div>
        )}
      </Droppable>
      <AddCardButton
        onClick={evt =>
          cardsDispatch({
            type: 'ADD',
            payload: {
              listId: list.id,
              text: 'new item',
              id: uuidv1(),
            },
          })
        }
      >
        + Add a card
      </AddCardButton>
    </Container>
  );
};

export default List;
