import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ITeam } from 'app/shared/model/team.model';
import { IDocument } from 'app/shared/model/document.model';

export interface IMessage {
  id?: number;
  email?: string | null;
  message?: string;
  sendToAllTeamMembers?: boolean;
  dateSent?: dayjs.Dayjs | null;
  toUserId?: IUser | null;
  fromUserId?: IUser | null;
  team?: ITeam | null;
  documents?: IDocument[] | null;
}

export const defaultValue: Readonly<IMessage> = {
  sendToAllTeamMembers: false,
};
