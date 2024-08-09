import { ICard, IList } from '../models';

export const cardsReducer = (state: ICard[], action: { type: string; payload: any }) => {
  const { listId, text, id } = action.payload;
  switch (action.type) {
    case 'ADD':
      const newCards = [...state, { listId, text, id }];
      return newCards;

    case 'EDIT':
      const cardsCopy = [...state];
      const { editValue } = action.payload;
      const foundCard = cardsCopy.find(card => card.id === id);
      if (foundCard) {
        foundCard.text = editValue;
      }
      return cardsCopy;

    case 'REMOVE':
      return state.filter(card => card.id !== id);

    case 'REORDER':
      const reordered = [...state.filter(card => card.listId !== listId), ...action.payload.reorderedCards];
      return reordered;

    case 'SET':
      return action.payload.newState;

    default:
      return state;
  }
};

export const listsReducer = (state: IList[], action: { type: string; payload: any }) => {
  const { id, listTitle } = action.payload;
  switch (action.type) {
    case 'ADD':
      return [...state, { id, listTitle }];
    case 'REMOVE':
      return state.filter(list => list.id !== id);

    case 'UPDATE_NAME':
      const listsCopy = [...state];
      const { value } = action.payload;
      const foundCard = listsCopy.find(card => card.id === id);
      if (foundCard) {
        foundCard.listTitle = value;
      }
      return listsCopy;
    default:
      return state;
  }
};
