package demo.JPA.service;

import demo.JPA.entity.OcrItem;
import demo.JPA.entity.Participant;
import demo.JPA.entity.Settlement;
import demo.JPA.entity.Vote;
import demo.JPA.repository.OcrItemRepository;
import demo.JPA.repository.ParticipantRepository;
import demo.JPA.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteCreationService {

    private final ParticipantRepository participantRepository;
    private final OcrItemRepository ocrItemRepository;
    private final VoteRepository voteRepository;

    /**
     * 특정 정산에 대한 초기 투표 레코드를 생성합니다.
     * 모든 참여자가 모든 아이템에 대해 'is_participated = false' 상태로 생성됩니다.
     */
    @Transactional
    public void createInitialVotes(Settlement settlement) {
        // 1. 해당 정산에 속한 모든 참여자를 조회합니다.
        List<Participant> participants = participantRepository.findBySettlementId(settlement.getId());

        // 2. 해당 정산에 속한 모든 아이템을 조회합니다.
        List<OcrItem> items = ocrItemRepository.findAllBySettlementId(settlement.getId());

        if (participants.isEmpty() || items.isEmpty()) {
            // 참여자나 아이템이 없으면 투표를 생성할 필요가 없습니다.
            return;
        }

        // 3. 모든 (참여자 x 아이템) 조합에 대한 Vote 엔티티 리스트를 만듭니다.
        List<Vote> newVotes = new ArrayList<>();
        for (Participant participant : participants) {
            for (OcrItem item : items) {
                Vote vote = Vote.builder()
                        .participant(participant)
                        .ocrItem(item)
                        .build();
                newVotes.add(vote);
            }
        }

        // 4. 생성된 모든 Vote 엔티티를 DB에 한번에 저장합니다. (Bulk Insert)
        voteRepository.saveAll(newVotes);
    }
}