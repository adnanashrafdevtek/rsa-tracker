import { IList, ICard } from '../models';
import { v1 as uuidv1 } from 'uuid';

export const reorder = <T>(list: T[], startIndex: number, endIndex: number) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};

export const initialLists: IList[] = [
  {
    id: 'id0',
    listTitle: 'Todo 📝',
  },
  {
    id: 'id1',
    listTitle: 'In progress 👌',
  },
  {
    id: 'id2',
    listTitle: 'Done ✅',
  },
];

export const initialCards: ICard[] = [
  {
    id: uuidv1(),
    text: 'Feed cat',
    listId: 'id0',
  },
  {
    id: uuidv1(),
    text: 'Take out bins',
    listId: 'id0',
  },
  {
    id: uuidv1(),
    text: 'Housework',
    listId: 'id1',
  },
];
