import dayjs from 'dayjs';
import { IMessage } from 'app/shared/model/message.model';
import { ITeam } from 'app/shared/model/team.model';

export interface IDocument {
  id?: number;
  name?: string;
  mimeType?: string;
  url?: string | null;
  createdDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  message?: IMessage | null;
  team?: ITeam | null;
}

export const defaultValue: Readonly<IDocument> = {};
